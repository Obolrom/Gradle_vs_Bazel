package com.romix.feature.feat437

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat437Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat437UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat437FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat437UserSummary
)

data class Feat437UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat437NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat437Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat437Config = Feat437Config()
) {

    fun loadSnapshot(userId: Long): Feat437NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat437NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat437UserSummary {
        return Feat437UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat437FeedItem> {
        val result = java.util.ArrayList<Feat437FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat437FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat437UiMapper {

    fun mapToUi(model: List<Feat437FeedItem>): Feat437UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat437UiModel(
            header = UiText("Feat437 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat437UiModel =
        Feat437UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat437UiModel =
        Feat437UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat437UiModel =
        Feat437UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat437Service(
    private val repository: Feat437Repository,
    private val uiMapper: Feat437UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat437UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat437UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat437UserItem1(val user: CoreUser, val label: String)
data class Feat437UserItem2(val user: CoreUser, val label: String)
data class Feat437UserItem3(val user: CoreUser, val label: String)
data class Feat437UserItem4(val user: CoreUser, val label: String)
data class Feat437UserItem5(val user: CoreUser, val label: String)
data class Feat437UserItem6(val user: CoreUser, val label: String)
data class Feat437UserItem7(val user: CoreUser, val label: String)
data class Feat437UserItem8(val user: CoreUser, val label: String)
data class Feat437UserItem9(val user: CoreUser, val label: String)
data class Feat437UserItem10(val user: CoreUser, val label: String)

data class Feat437StateBlock1(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock2(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock3(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock4(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock5(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock6(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock7(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock8(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock9(val state: Feat437UiModel, val checksum: Int)
data class Feat437StateBlock10(val state: Feat437UiModel, val checksum: Int)

fun buildFeat437UserItem(user: CoreUser, index: Int): Feat437UserItem1 {
    return Feat437UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat437StateBlock(model: Feat437UiModel): Feat437StateBlock1 {
    return Feat437StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat437UserSummary> {
    val list = java.util.ArrayList<Feat437UserSummary>(users.size)
    for (user in users) {
        list += Feat437UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat437UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat437UiModel {
    val summaries = (0 until count).map {
        Feat437UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat437UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat437UiModel> {
    val models = java.util.ArrayList<Feat437UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat437AnalyticsEvent1(val name: String, val value: String)
data class Feat437AnalyticsEvent2(val name: String, val value: String)
data class Feat437AnalyticsEvent3(val name: String, val value: String)
data class Feat437AnalyticsEvent4(val name: String, val value: String)
data class Feat437AnalyticsEvent5(val name: String, val value: String)
data class Feat437AnalyticsEvent6(val name: String, val value: String)
data class Feat437AnalyticsEvent7(val name: String, val value: String)
data class Feat437AnalyticsEvent8(val name: String, val value: String)
data class Feat437AnalyticsEvent9(val name: String, val value: String)
data class Feat437AnalyticsEvent10(val name: String, val value: String)

fun logFeat437Event1(event: Feat437AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat437Event2(event: Feat437AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat437Event3(event: Feat437AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat437Event4(event: Feat437AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat437Event5(event: Feat437AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat437Event6(event: Feat437AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat437Event7(event: Feat437AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat437Event8(event: Feat437AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat437Event9(event: Feat437AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat437Event10(event: Feat437AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat437Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat437Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat437(u: CoreUser): Feat437Projection1 =
    Feat437Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat437Projection1> {
    val list = java.util.ArrayList<Feat437Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat437(u)
    }
    return list
}
