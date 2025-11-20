package com.romix.feature.feat670

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat670Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat670UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat670FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat670UserSummary
)

data class Feat670UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat670NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat670Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat670Config = Feat670Config()
) {

    fun loadSnapshot(userId: Long): Feat670NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat670NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat670UserSummary {
        return Feat670UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat670FeedItem> {
        val result = java.util.ArrayList<Feat670FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat670FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat670UiMapper {

    fun mapToUi(model: List<Feat670FeedItem>): Feat670UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat670UiModel(
            header = UiText("Feat670 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat670UiModel =
        Feat670UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat670UiModel =
        Feat670UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat670UiModel =
        Feat670UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat670Service(
    private val repository: Feat670Repository,
    private val uiMapper: Feat670UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat670UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat670UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat670UserItem1(val user: CoreUser, val label: String)
data class Feat670UserItem2(val user: CoreUser, val label: String)
data class Feat670UserItem3(val user: CoreUser, val label: String)
data class Feat670UserItem4(val user: CoreUser, val label: String)
data class Feat670UserItem5(val user: CoreUser, val label: String)
data class Feat670UserItem6(val user: CoreUser, val label: String)
data class Feat670UserItem7(val user: CoreUser, val label: String)
data class Feat670UserItem8(val user: CoreUser, val label: String)
data class Feat670UserItem9(val user: CoreUser, val label: String)
data class Feat670UserItem10(val user: CoreUser, val label: String)

data class Feat670StateBlock1(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock2(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock3(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock4(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock5(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock6(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock7(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock8(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock9(val state: Feat670UiModel, val checksum: Int)
data class Feat670StateBlock10(val state: Feat670UiModel, val checksum: Int)

fun buildFeat670UserItem(user: CoreUser, index: Int): Feat670UserItem1 {
    return Feat670UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat670StateBlock(model: Feat670UiModel): Feat670StateBlock1 {
    return Feat670StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat670UserSummary> {
    val list = java.util.ArrayList<Feat670UserSummary>(users.size)
    for (user in users) {
        list += Feat670UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat670UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat670UiModel {
    val summaries = (0 until count).map {
        Feat670UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat670UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat670UiModel> {
    val models = java.util.ArrayList<Feat670UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat670AnalyticsEvent1(val name: String, val value: String)
data class Feat670AnalyticsEvent2(val name: String, val value: String)
data class Feat670AnalyticsEvent3(val name: String, val value: String)
data class Feat670AnalyticsEvent4(val name: String, val value: String)
data class Feat670AnalyticsEvent5(val name: String, val value: String)
data class Feat670AnalyticsEvent6(val name: String, val value: String)
data class Feat670AnalyticsEvent7(val name: String, val value: String)
data class Feat670AnalyticsEvent8(val name: String, val value: String)
data class Feat670AnalyticsEvent9(val name: String, val value: String)
data class Feat670AnalyticsEvent10(val name: String, val value: String)

fun logFeat670Event1(event: Feat670AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat670Event2(event: Feat670AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat670Event3(event: Feat670AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat670Event4(event: Feat670AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat670Event5(event: Feat670AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat670Event6(event: Feat670AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat670Event7(event: Feat670AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat670Event8(event: Feat670AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat670Event9(event: Feat670AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat670Event10(event: Feat670AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat670Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat670Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat670(u: CoreUser): Feat670Projection1 =
    Feat670Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat670Projection1> {
    val list = java.util.ArrayList<Feat670Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat670(u)
    }
    return list
}
