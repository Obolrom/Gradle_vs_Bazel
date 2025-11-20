package com.romix.feature.feat523

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat523Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat523UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat523FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat523UserSummary
)

data class Feat523UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat523NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat523Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat523Config = Feat523Config()
) {

    fun loadSnapshot(userId: Long): Feat523NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat523NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat523UserSummary {
        return Feat523UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat523FeedItem> {
        val result = java.util.ArrayList<Feat523FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat523FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat523UiMapper {

    fun mapToUi(model: List<Feat523FeedItem>): Feat523UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat523UiModel(
            header = UiText("Feat523 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat523UiModel =
        Feat523UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat523UiModel =
        Feat523UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat523UiModel =
        Feat523UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat523Service(
    private val repository: Feat523Repository,
    private val uiMapper: Feat523UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat523UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat523UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat523UserItem1(val user: CoreUser, val label: String)
data class Feat523UserItem2(val user: CoreUser, val label: String)
data class Feat523UserItem3(val user: CoreUser, val label: String)
data class Feat523UserItem4(val user: CoreUser, val label: String)
data class Feat523UserItem5(val user: CoreUser, val label: String)
data class Feat523UserItem6(val user: CoreUser, val label: String)
data class Feat523UserItem7(val user: CoreUser, val label: String)
data class Feat523UserItem8(val user: CoreUser, val label: String)
data class Feat523UserItem9(val user: CoreUser, val label: String)
data class Feat523UserItem10(val user: CoreUser, val label: String)

data class Feat523StateBlock1(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock2(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock3(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock4(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock5(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock6(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock7(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock8(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock9(val state: Feat523UiModel, val checksum: Int)
data class Feat523StateBlock10(val state: Feat523UiModel, val checksum: Int)

fun buildFeat523UserItem(user: CoreUser, index: Int): Feat523UserItem1 {
    return Feat523UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat523StateBlock(model: Feat523UiModel): Feat523StateBlock1 {
    return Feat523StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat523UserSummary> {
    val list = java.util.ArrayList<Feat523UserSummary>(users.size)
    for (user in users) {
        list += Feat523UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat523UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat523UiModel {
    val summaries = (0 until count).map {
        Feat523UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat523UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat523UiModel> {
    val models = java.util.ArrayList<Feat523UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat523AnalyticsEvent1(val name: String, val value: String)
data class Feat523AnalyticsEvent2(val name: String, val value: String)
data class Feat523AnalyticsEvent3(val name: String, val value: String)
data class Feat523AnalyticsEvent4(val name: String, val value: String)
data class Feat523AnalyticsEvent5(val name: String, val value: String)
data class Feat523AnalyticsEvent6(val name: String, val value: String)
data class Feat523AnalyticsEvent7(val name: String, val value: String)
data class Feat523AnalyticsEvent8(val name: String, val value: String)
data class Feat523AnalyticsEvent9(val name: String, val value: String)
data class Feat523AnalyticsEvent10(val name: String, val value: String)

fun logFeat523Event1(event: Feat523AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat523Event2(event: Feat523AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat523Event3(event: Feat523AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat523Event4(event: Feat523AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat523Event5(event: Feat523AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat523Event6(event: Feat523AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat523Event7(event: Feat523AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat523Event8(event: Feat523AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat523Event9(event: Feat523AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat523Event10(event: Feat523AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat523Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat523Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat523(u: CoreUser): Feat523Projection1 =
    Feat523Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat523Projection1> {
    val list = java.util.ArrayList<Feat523Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat523(u)
    }
    return list
}
