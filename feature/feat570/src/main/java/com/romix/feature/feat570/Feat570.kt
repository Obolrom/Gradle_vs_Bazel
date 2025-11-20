package com.romix.feature.feat570

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat570Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat570UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat570FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat570UserSummary
)

data class Feat570UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat570NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat570Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat570Config = Feat570Config()
) {

    fun loadSnapshot(userId: Long): Feat570NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat570NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat570UserSummary {
        return Feat570UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat570FeedItem> {
        val result = java.util.ArrayList<Feat570FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat570FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat570UiMapper {

    fun mapToUi(model: List<Feat570FeedItem>): Feat570UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat570UiModel(
            header = UiText("Feat570 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat570UiModel =
        Feat570UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat570UiModel =
        Feat570UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat570UiModel =
        Feat570UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat570Service(
    private val repository: Feat570Repository,
    private val uiMapper: Feat570UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat570UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat570UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat570UserItem1(val user: CoreUser, val label: String)
data class Feat570UserItem2(val user: CoreUser, val label: String)
data class Feat570UserItem3(val user: CoreUser, val label: String)
data class Feat570UserItem4(val user: CoreUser, val label: String)
data class Feat570UserItem5(val user: CoreUser, val label: String)
data class Feat570UserItem6(val user: CoreUser, val label: String)
data class Feat570UserItem7(val user: CoreUser, val label: String)
data class Feat570UserItem8(val user: CoreUser, val label: String)
data class Feat570UserItem9(val user: CoreUser, val label: String)
data class Feat570UserItem10(val user: CoreUser, val label: String)

data class Feat570StateBlock1(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock2(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock3(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock4(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock5(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock6(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock7(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock8(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock9(val state: Feat570UiModel, val checksum: Int)
data class Feat570StateBlock10(val state: Feat570UiModel, val checksum: Int)

fun buildFeat570UserItem(user: CoreUser, index: Int): Feat570UserItem1 {
    return Feat570UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat570StateBlock(model: Feat570UiModel): Feat570StateBlock1 {
    return Feat570StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat570UserSummary> {
    val list = java.util.ArrayList<Feat570UserSummary>(users.size)
    for (user in users) {
        list += Feat570UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat570UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat570UiModel {
    val summaries = (0 until count).map {
        Feat570UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat570UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat570UiModel> {
    val models = java.util.ArrayList<Feat570UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat570AnalyticsEvent1(val name: String, val value: String)
data class Feat570AnalyticsEvent2(val name: String, val value: String)
data class Feat570AnalyticsEvent3(val name: String, val value: String)
data class Feat570AnalyticsEvent4(val name: String, val value: String)
data class Feat570AnalyticsEvent5(val name: String, val value: String)
data class Feat570AnalyticsEvent6(val name: String, val value: String)
data class Feat570AnalyticsEvent7(val name: String, val value: String)
data class Feat570AnalyticsEvent8(val name: String, val value: String)
data class Feat570AnalyticsEvent9(val name: String, val value: String)
data class Feat570AnalyticsEvent10(val name: String, val value: String)

fun logFeat570Event1(event: Feat570AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat570Event2(event: Feat570AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat570Event3(event: Feat570AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat570Event4(event: Feat570AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat570Event5(event: Feat570AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat570Event6(event: Feat570AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat570Event7(event: Feat570AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat570Event8(event: Feat570AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat570Event9(event: Feat570AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat570Event10(event: Feat570AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat570Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat570Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat570(u: CoreUser): Feat570Projection1 =
    Feat570Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat570Projection1> {
    val list = java.util.ArrayList<Feat570Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat570(u)
    }
    return list
}
