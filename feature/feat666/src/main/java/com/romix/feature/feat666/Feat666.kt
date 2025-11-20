package com.romix.feature.feat666

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat666Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat666UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat666FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat666UserSummary
)

data class Feat666UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat666NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat666Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat666Config = Feat666Config()
) {

    fun loadSnapshot(userId: Long): Feat666NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat666NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat666UserSummary {
        return Feat666UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat666FeedItem> {
        val result = java.util.ArrayList<Feat666FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat666FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat666UiMapper {

    fun mapToUi(model: List<Feat666FeedItem>): Feat666UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat666UiModel(
            header = UiText("Feat666 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat666UiModel =
        Feat666UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat666UiModel =
        Feat666UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat666UiModel =
        Feat666UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat666Service(
    private val repository: Feat666Repository,
    private val uiMapper: Feat666UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat666UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat666UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat666UserItem1(val user: CoreUser, val label: String)
data class Feat666UserItem2(val user: CoreUser, val label: String)
data class Feat666UserItem3(val user: CoreUser, val label: String)
data class Feat666UserItem4(val user: CoreUser, val label: String)
data class Feat666UserItem5(val user: CoreUser, val label: String)
data class Feat666UserItem6(val user: CoreUser, val label: String)
data class Feat666UserItem7(val user: CoreUser, val label: String)
data class Feat666UserItem8(val user: CoreUser, val label: String)
data class Feat666UserItem9(val user: CoreUser, val label: String)
data class Feat666UserItem10(val user: CoreUser, val label: String)

data class Feat666StateBlock1(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock2(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock3(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock4(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock5(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock6(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock7(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock8(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock9(val state: Feat666UiModel, val checksum: Int)
data class Feat666StateBlock10(val state: Feat666UiModel, val checksum: Int)

fun buildFeat666UserItem(user: CoreUser, index: Int): Feat666UserItem1 {
    return Feat666UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat666StateBlock(model: Feat666UiModel): Feat666StateBlock1 {
    return Feat666StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat666UserSummary> {
    val list = java.util.ArrayList<Feat666UserSummary>(users.size)
    for (user in users) {
        list += Feat666UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat666UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat666UiModel {
    val summaries = (0 until count).map {
        Feat666UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat666UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat666UiModel> {
    val models = java.util.ArrayList<Feat666UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat666AnalyticsEvent1(val name: String, val value: String)
data class Feat666AnalyticsEvent2(val name: String, val value: String)
data class Feat666AnalyticsEvent3(val name: String, val value: String)
data class Feat666AnalyticsEvent4(val name: String, val value: String)
data class Feat666AnalyticsEvent5(val name: String, val value: String)
data class Feat666AnalyticsEvent6(val name: String, val value: String)
data class Feat666AnalyticsEvent7(val name: String, val value: String)
data class Feat666AnalyticsEvent8(val name: String, val value: String)
data class Feat666AnalyticsEvent9(val name: String, val value: String)
data class Feat666AnalyticsEvent10(val name: String, val value: String)

fun logFeat666Event1(event: Feat666AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat666Event2(event: Feat666AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat666Event3(event: Feat666AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat666Event4(event: Feat666AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat666Event5(event: Feat666AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat666Event6(event: Feat666AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat666Event7(event: Feat666AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat666Event8(event: Feat666AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat666Event9(event: Feat666AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat666Event10(event: Feat666AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat666Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat666Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat666(u: CoreUser): Feat666Projection1 =
    Feat666Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat666Projection1> {
    val list = java.util.ArrayList<Feat666Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat666(u)
    }
    return list
}
