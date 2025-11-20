package com.romix.feature.feat84

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat84Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat84UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat84FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat84UserSummary
)

data class Feat84UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat84NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat84Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat84Config = Feat84Config()
) {

    fun loadSnapshot(userId: Long): Feat84NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat84NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat84UserSummary {
        return Feat84UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat84FeedItem> {
        val result = java.util.ArrayList<Feat84FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat84FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat84UiMapper {

    fun mapToUi(model: List<Feat84FeedItem>): Feat84UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat84UiModel(
            header = UiText("Feat84 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat84UiModel =
        Feat84UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat84UiModel =
        Feat84UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat84UiModel =
        Feat84UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat84Service(
    private val repository: Feat84Repository,
    private val uiMapper: Feat84UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat84UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat84UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat84UserItem1(val user: CoreUser, val label: String)
data class Feat84UserItem2(val user: CoreUser, val label: String)
data class Feat84UserItem3(val user: CoreUser, val label: String)
data class Feat84UserItem4(val user: CoreUser, val label: String)
data class Feat84UserItem5(val user: CoreUser, val label: String)
data class Feat84UserItem6(val user: CoreUser, val label: String)
data class Feat84UserItem7(val user: CoreUser, val label: String)
data class Feat84UserItem8(val user: CoreUser, val label: String)
data class Feat84UserItem9(val user: CoreUser, val label: String)
data class Feat84UserItem10(val user: CoreUser, val label: String)

data class Feat84StateBlock1(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock2(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock3(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock4(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock5(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock6(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock7(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock8(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock9(val state: Feat84UiModel, val checksum: Int)
data class Feat84StateBlock10(val state: Feat84UiModel, val checksum: Int)

fun buildFeat84UserItem(user: CoreUser, index: Int): Feat84UserItem1 {
    return Feat84UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat84StateBlock(model: Feat84UiModel): Feat84StateBlock1 {
    return Feat84StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat84UserSummary> {
    val list = java.util.ArrayList<Feat84UserSummary>(users.size)
    for (user in users) {
        list += Feat84UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat84UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat84UiModel {
    val summaries = (0 until count).map {
        Feat84UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat84UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat84UiModel> {
    val models = java.util.ArrayList<Feat84UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat84AnalyticsEvent1(val name: String, val value: String)
data class Feat84AnalyticsEvent2(val name: String, val value: String)
data class Feat84AnalyticsEvent3(val name: String, val value: String)
data class Feat84AnalyticsEvent4(val name: String, val value: String)
data class Feat84AnalyticsEvent5(val name: String, val value: String)
data class Feat84AnalyticsEvent6(val name: String, val value: String)
data class Feat84AnalyticsEvent7(val name: String, val value: String)
data class Feat84AnalyticsEvent8(val name: String, val value: String)
data class Feat84AnalyticsEvent9(val name: String, val value: String)
data class Feat84AnalyticsEvent10(val name: String, val value: String)

fun logFeat84Event1(event: Feat84AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat84Event2(event: Feat84AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat84Event3(event: Feat84AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat84Event4(event: Feat84AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat84Event5(event: Feat84AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat84Event6(event: Feat84AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat84Event7(event: Feat84AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat84Event8(event: Feat84AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat84Event9(event: Feat84AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat84Event10(event: Feat84AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat84Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat84Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat84(u: CoreUser): Feat84Projection1 =
    Feat84Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat84Projection1> {
    val list = java.util.ArrayList<Feat84Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat84(u)
    }
    return list
}
