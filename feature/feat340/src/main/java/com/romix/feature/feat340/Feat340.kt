package com.romix.feature.feat340

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat340Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat340UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat340FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat340UserSummary
)

data class Feat340UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat340NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat340Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat340Config = Feat340Config()
) {

    fun loadSnapshot(userId: Long): Feat340NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat340NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat340UserSummary {
        return Feat340UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat340FeedItem> {
        val result = java.util.ArrayList<Feat340FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat340FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat340UiMapper {

    fun mapToUi(model: List<Feat340FeedItem>): Feat340UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat340UiModel(
            header = UiText("Feat340 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat340UiModel =
        Feat340UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat340UiModel =
        Feat340UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat340UiModel =
        Feat340UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat340Service(
    private val repository: Feat340Repository,
    private val uiMapper: Feat340UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat340UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat340UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat340UserItem1(val user: CoreUser, val label: String)
data class Feat340UserItem2(val user: CoreUser, val label: String)
data class Feat340UserItem3(val user: CoreUser, val label: String)
data class Feat340UserItem4(val user: CoreUser, val label: String)
data class Feat340UserItem5(val user: CoreUser, val label: String)
data class Feat340UserItem6(val user: CoreUser, val label: String)
data class Feat340UserItem7(val user: CoreUser, val label: String)
data class Feat340UserItem8(val user: CoreUser, val label: String)
data class Feat340UserItem9(val user: CoreUser, val label: String)
data class Feat340UserItem10(val user: CoreUser, val label: String)

data class Feat340StateBlock1(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock2(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock3(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock4(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock5(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock6(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock7(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock8(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock9(val state: Feat340UiModel, val checksum: Int)
data class Feat340StateBlock10(val state: Feat340UiModel, val checksum: Int)

fun buildFeat340UserItem(user: CoreUser, index: Int): Feat340UserItem1 {
    return Feat340UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat340StateBlock(model: Feat340UiModel): Feat340StateBlock1 {
    return Feat340StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat340UserSummary> {
    val list = java.util.ArrayList<Feat340UserSummary>(users.size)
    for (user in users) {
        list += Feat340UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat340UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat340UiModel {
    val summaries = (0 until count).map {
        Feat340UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat340UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat340UiModel> {
    val models = java.util.ArrayList<Feat340UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat340AnalyticsEvent1(val name: String, val value: String)
data class Feat340AnalyticsEvent2(val name: String, val value: String)
data class Feat340AnalyticsEvent3(val name: String, val value: String)
data class Feat340AnalyticsEvent4(val name: String, val value: String)
data class Feat340AnalyticsEvent5(val name: String, val value: String)
data class Feat340AnalyticsEvent6(val name: String, val value: String)
data class Feat340AnalyticsEvent7(val name: String, val value: String)
data class Feat340AnalyticsEvent8(val name: String, val value: String)
data class Feat340AnalyticsEvent9(val name: String, val value: String)
data class Feat340AnalyticsEvent10(val name: String, val value: String)

fun logFeat340Event1(event: Feat340AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat340Event2(event: Feat340AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat340Event3(event: Feat340AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat340Event4(event: Feat340AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat340Event5(event: Feat340AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat340Event6(event: Feat340AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat340Event7(event: Feat340AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat340Event8(event: Feat340AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat340Event9(event: Feat340AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat340Event10(event: Feat340AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat340Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat340Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat340(u: CoreUser): Feat340Projection1 =
    Feat340Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat340Projection1> {
    val list = java.util.ArrayList<Feat340Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat340(u)
    }
    return list
}
